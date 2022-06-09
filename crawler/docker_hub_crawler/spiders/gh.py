from github import Github
from github import GithubException

class GithubOperation():
    """
    A wrapper function to wrap the PyGithub
    """

    # Here we don't use any token, because we do
    # no sensitive operation
    g = Github()

    def search_repository(self, repository: str) -> object:
        """
        Search repository with the DockerHub image name and use
        the only first search result. This may seem stupid, but
        at this moment, this is the best way to think the GitHub
        search functionality.

        see https://docs.github.com/en/rest/search#search-repositories

        """
        return self.g.search_repositories(repository)[0]

    def get_readme(self, repository: object) -> str:
        """
        From repositories to get the README file.
        """
        content = None

        try:
            content = repository.get_contents("README.md")
        except GithubException:
            # Should not throw any error, just pass
            pass

        if content is None:
            return ""
        else:
            # The content.decoded_content return binary string
            # so use decode() to get the string
            return content.decoded_content.decode()

    def download_readme(self, repository: str) -> str:
        """
        A wrapper function
        """
        repository_iter = self.search_repository(repository)
        return self.get_readme(repository_iter)

# For unit testing
if __name__ == '__main__':
    g = GithubOperation()
    print(g.download_readme("shejialuo/CS144"))
    print(g.download_readme("shejialuo/shejialuo"))
