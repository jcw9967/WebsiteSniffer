# Website Sniffer
This program is designed to assist with the probing of a large number of websites using IPv4 and IPv6, if available.

## Installation
Download the WebsiteSniffer.jar file from the releases section, or alternatively compile the archive yourself using Intellij IDEA, then place it into its own directory.

Due to the lack of free GEOIP APIs providing huge numbers of requests, a local database is being used instead. To install this database, head over to [GeoLite2](http://dev.maxmind.com/geoip/geoip2/geolite2/) and download the "GeoLite2 City" MaxMind DB. Unzip this database and place it in the same directory as the WebsiteSniffer.jar.

## Usage
```
usage: java -jar <FILENAME> [options]
 -cli                   Use the command line interface
 -f,--urlfile <FILE>    Add URLs from a given file
 -h,--help              Display this help menu
 -t,--threads <COUNT>   Specify the number of concurrent threads to use.
                        Default = 50.
```

If the `-cli` option isn't specified, a GUI will be used instead of the CLI.

## What else?
Website Sniffer uses SQLite for its storage database. To retrieve data from it download the SQLite binary [here](https://www.sqlite.org/download.html). Once downloaded open the database file using `.open <FILENAME.db>`. The database schema can be viewed using `.fullschema`.

Each URL in the url list must be line separated, like this:
```
google.com
youtube.com
facebook.com
...
```

## Example
To run the sniffer using a predefined list of URLs use the following command:
```
java -jar WebsiteSniffer.jar -cli -f <FILENAME>
```
