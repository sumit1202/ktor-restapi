# ktor-restapi

A simple rest api handling CRUD operations with postgres database.

Connected postgres database (2 dbs: animals, users) using ktorm framework.
Implemented /animals end point and handled get, post, put, delete http requests (used Postman).
Implemented /register endpoint : post request : encrypted password via bCrypt library.
Implemented /login endpoint : post request : added validation, decrypted password via bCrypt library.
Implemented /me protected endpoint: get request: utilized JWT.

Refrence: https://youtube.com/playlist?list=PLFmuMD2V4CkyR0Pa42Cqu5mIhH17uG8nN
