import scrapy, json, requests

from .gh import GithubOperation

from .custom_settings import request_headers

from .db import MySql

class DockerHubSpider(scrapy.Spider):

    name = "spider"

    custom_settings = request_headers

    sql = MySql()

    gh = GithubOperation()

    long_description_url = [
        'https://hub.docker.com/v2/repositories/',
        'https://hub.docker.com/api/content/v1/products/images/'
    ]

    official_image_url = 'https://hub.docker.com/api/content/v1/products/search?page_size=25&image_filter=official&type=image&page='

    verified_publisher_image_url = 'https://hub.docker.com/api/content/v1/products/search?image_filter=store&page_size=25&q=&type=image&page='

    other_image_url_head = 'https://hub.docker.com/api/content/v1/products/search?page_size=25&q='

    other_image_url_tail = '&type=image&page='

    def start_requests(self):

        # self.sql.connect()

        official_image_url_list = [
            self.official_image_url + str(page + 1) for page in range(7)
        ]

        verified_publisher_image_url_list = [
            self.verified_publisher_image_url + str(page + 1) for page in range(100)
        ]

        other_image_url_list = []

        # Here, we brutely make the `other_image_url_list`'s query parameter
        # from aa to zz, and page query from 1 to 100. This may seem ugly, but
        # it is useful.
        for i in range(ord("a"), ord("z") + 1):
            for j in range(ord("a"), ord("z") + 1):
                for page in range(100):
                    other_image_url_list.append(
                        self.other_image_url_head + chr(i) + chr(j)
                        + self.other_image_url_tail + str(page+1)
                    )
        # Here, we brutely make the `other_image_url_list`'s query parameter
        # from aaa to zzz, and page query from 1 to 100. This may seem ugly, but
        # it is useful.
        for i in range(ord("a"), ord("z") + 1):
            for j in range(ord("a"), ord("z") + 1):
                for k in range(ord("a"), ord("z") + 1):
                    for page in range(100):
                        other_image_url_list.append(
                            self.other_image_url_head + chr(i) + chr(j)
                            + chr(k) + self.other_image_url_tail + str(page+1)
                        )

        urls = official_image_url_list + verified_publisher_image_url_list + other_image_url_list

        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):

        response_json = json.loads(response.body)
        image_infos = response_json["summaries"]
        for image_info in image_infos:

            name = image_info["name"]
            temp_name = list(name)

            index = name.find("/")

            if index != -1:
                temp_name[index] = "_"
                name = "".join(temp_name)

            image_info_need_inserted = [
                image_info["name"],
                image_info["short_description"],
                "dockerhub_data/" + name,
                image_info["filter_type"],
                "github_data/" + name,
            ]
            sql = (f'INSERT INTO image_info VALUES('
                   f'"{image_info_need_inserted[0]}",'
                   f'"{image_info_need_inserted[1]}",'
                   f'"{image_info_need_inserted[2]}",'
                   f'"{image_info_need_inserted[3]}")')

            # Well, should use exception here...
            if self.sql.insert(sql) == False:
                continue

            url = None

            if image_info_need_inserted[3] == 'community':
                url = self.long_description_url[0] + image_info['slug']
            elif image_info_need_inserted[3] == 'official':
                url = self.long_description_url[0] + 'library/' + image_info['slug']
            else:
                url = self.long_description_url[1] + image_info['slug']
                r = requests.get(url = url)
                if r.status_code != 200:
                    url = self.long_description_url[0] + image_info['slug']

            yield scrapy.Request(url = url, callback = self.long_description_handle)

    def long_description_handle(self, response):
        """
        To get the long description from the previous response.
        If the long description is short, it should do request to
        GitHub for find some more information.
        """

        response_json = json.loads(response.body)

        temp_filename = None

        if "user" in response_json:
            if response_json["user"] == "library":
                temp_filename = response_json["name"]
            else:
                temp_filename = response_json["user"] + "_" + response_json["name"]
        else:
            temp_filename = response_json["name"]

        search_repository = f'{temp_filename}'
        filename = f'dockerhub_data/{temp_filename}.md'

        with open(filename, 'w') as f:
            if response_json["full_description"] != None:
                f.write(response_json["full_description"])
            else:
                f.write("")
            f.close()

        # Now, it needs to search from the GitHub
        github_readme = self.gh.download_readme(search_repository)
        filename = f'github_data/{temp_filename}.md'
        with open(filename, 'w') as f:
            f.write(github_readme)
            f.close()
