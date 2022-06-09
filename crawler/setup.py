import sys
from setuptools import setup, find_packages

if sys.version_info < (3, 6):
    raise Exception("Python 3.6 or higher is required. Your version is %s." % sys.version)

__version__ = "1.0.0"

long_description = open('README.md', encoding='utf-8').read()

setup(
    name='docker-hub-crawler',
    packages=find_packages(exclude=["*.tests", "*.tests.*", "tests.*", "tests"]),
    version=__version__,
    description='DockerHub image short description and long description crawler',
    long_description=long_description,
    include_package_data=True,
    python_requires='>=3.6',
    install_requires=[
        "scrapy", "pygithub", "pymysql"
    ],
)
