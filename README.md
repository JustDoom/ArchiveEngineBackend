# ArchiveEngine

[![Discord Server](https://img.shields.io/discord/979589333524820018?color=7289da&label=EimerArchive&style=flat-square&logo=appveyor)](https://discord.gg/k8RcgxpnBS)

This project aims to provide a basic search engine for the Wayback Machine part of archive.org. You are able to look for
other urls that begin with a specific url but that is limited to 10,000 results which may not be enough in most cases.
So I decided to try and make a "search engine" for it myself. Currently, it only uses data from the url itself.

## Why

Archive.org/The Wayback Machine is an amazing service but they are really lacking in the API and searchability
department. I thought if there was something that allows me to search for archived links with a certain word in the url
it would make finding certain lost files so much easier (Mainly Minecraft related ones).

## How it works

This uses the [CDX API](https://github.com/internetarchive/wayback/tree/master/wayback-cdx-server) to fetch URLs to be
indexed. It used to use some other endpoint, but it was extremely slow and unreliable. This new system is at least 10
times faster in most cases. Also, a lot more reliable.

The new system also allows finding any URL under the domains subdomains, so it can find URLs under any subdomains as
well even if you didn't know it had any!

## TODO

### API

- [ ] Write the TODO :P

### Indexer

- [x] Indexing of links from a certain domain
- [x] Re-try indexing of failed requests. Status code errors or even just timeouts!
- [x] Basic multithreading for fetching and indexing URLs
- [x] Stop duplicate links
- [ ] Even more speed somehow
- [ ] Queue up domains to search through

## Contributing

TODO