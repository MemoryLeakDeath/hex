#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER ${dbuser} WITH PASSWORD '${dbpassword}';
	CREATE DATABASE ${dbname} WITH OWNER = ${dbuser} ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;
	GRANT ALL PRIVILEGES ON DATABASE ${dbname} TO ${dbuser};
	CREATE USER hextester WITH PASSWORD '${dbpassword}';
	CREATE DATABASE hextest WITH OWNER = hextester ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;
	GRANT ALL PRIVILEGES ON DATABASE hextest TO hextester;
EOSQL