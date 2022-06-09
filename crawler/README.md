# Crawler

Crawler is aimed at pulling short description
and full description of different kinds of images from DockerHub.
When the DockerHub's information is not enough, it will find
information by the GitHub API

This project is based on Scrapy.

## DockerHub Request Header

You need to edit `request_headers` in `custom_settings.py` from your
browser for crawling, the thing you need to change is `cookie`, you
should log into the DockerHub to get the cookie for the crawler working
properly

## Mysql

In `db.py`, you could configure the database parameter for your
preference. In this project:

+ host: `localhost`
+ user: `root`
+ password: `root`
+ database: `dockerhub_info`

And you need to create table:

```sql
CREATE TABLE image_info (
    name VARCHAR(200),
    short_desc VARCHAR(200),
    `desc` VARCHAR(200),
    `type` VARCHAR(200),
    PRIMARY KEY ( name )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
