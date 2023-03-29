# ArchiveEngine
 
This is a project to store all the archived links in the wayback machine in a place they can be searched through.  
When running this it can take a while to get the response so please wait a while.

Currently the code sucks but I wanted to get a working version. I will clean it up later.

There is a live version of this up at https://search.imjustdoom.com (hopefully online)

## Why

Archive.org/The wayback machine is an amazing service but they are really lacking in the API and searchability department. I thought if there was something that allows me to search for archived links with a certain word in the url it would make finding certain lost files so much easier (Mainly Minecraft related ones).

## How it works

This indexes the links by making requests to the limited API they have. It makes a request to certain timeframes to get managble chunks of links, I can remove the 10k url limit but I don't want to kill archive.org or be blocked.