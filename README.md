# ArchiveEngine

[![Discord Server](https://img.shields.io/discord/810752039470235688?color=7289da&label=MY%20DISCORD&style=flat-square&logo=appveyor)](https://discord.gg/ydGK5jYV6t)
[![Discord Server](https://img.shields.io/discord/979589333524820018?color=7289da&label=MY%20ARCHIVE%20DISCORD&style=flat-square&logo=appveyor)](https://discord.gg/k8RcgxpnBS)
 
This is a project to store all the archived links in the wayback machine in a place they can be searched through.  
When running this it can take a while to get the response so please wait a while.

Currently the code sucks but I wanted to get a working version. I will clean it up later.

There is a live version of this up at https://search.imjustdoom.com (hopefully online)

## Why

Archive.org/The wayback machine is an amazing service but they are really lacking in the API and searchability department. I thought if there was something that allows me to search for archived links with a certain word in the url it would make finding certain lost files so much easier (Mainly Minecraft related ones).

## How it works

This indexes the links by making requests to the limited API they have. It makes a request to certain timeframes to get managble chunks of links, I can remove the 10k url limit but I don't want to kill archive.org or be blocked.

This project contains two parts, the indexer and the actual API. The indexer is the part that indexes the links and stores them in the database. The API allows you to make requests and search the database for certain links.

## TODO

General feature TODO, doesn't include code cleanups, small fixes and stuff.

### API
- [x] Basic way to search for links
- [x] Return the link along with the timestamp, mimetype and status code
- [x] A way to sort by the stored information (timestamp, mimetype, status code and url)
- [ ] Allow searching with options/filters. Includes excluding results with certain words
- [x] Add link pagination
- [ ] Use a proper search algorithm/engine (elasticsearch/meilisearch)
- [ ] Proper error handling and returning responses
- [ ] Allow changing of the page size

### Indexer

- [x] Indexing of links from a certain domain
- [x] Increment the timestamp automatically when a request is done
- [ ] Re-try indexing of failed requests (Usually 504 errors)
- [x] Stop indexing at a certain date
- [x] Allow controlling of basic features (If indexer is running, domain to index, start timestamp?) with startup arguments
- [ ] Allow indexing of multiple domains at once. Not sure of the request limit to archive.org's API
- [ ] Stop duplicate links, links can be in it multiple times (links that change like domain.com/latestDownload) but not if everything is the same (timestamp, link, mimeType, status code etc) as it would be the exact same link.