#!/bin/bash

DB_DUMP_LOCATION="/tmp/psql_data/snpseek_3kfiltered_chr09.sql"

echo "*** CREATING DATABASE ***"

psql -U postgres -d iric < "$DB_DUMP_LOCATION";

echo "*** DATABASE CREATED! ***"