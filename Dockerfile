FROM ubuntu:latest
LABEL authors="holda"

ENTRYPOINT ["top", "-b"]