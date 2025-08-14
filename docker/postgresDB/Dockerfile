FROM postgres:15.5

RUN mkdir -p /tmp/psql_data/

COPY snpseek_3kfiltered_chr09.sql /tmp/psql_data/
COPY scripts/01-restore.sh /docker-entrypoint-initdb.d/