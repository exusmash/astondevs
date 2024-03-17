Role:
GET http://localhost:8080/role/all

GET http://localhost:8080/role/{id}

POST http://localhost:8080/role

{ "name": "New role name" }

DELETE http://localhost:8080/role/{id}

PUT http://localhost:8080/role

{ "id": 1, "name": "new name" }

Access Log:
GET http://localhost:8080/accessLog/all

GET http://localhost:8080/accessLog/{id}

POST http://localhost:8080/accessLog
{ "description": "new empty log" }

DELETE http://localhost:8080/accessLog/{id}

PUT http://localhost:8080/accessLog  { "id": 5, "description": "new empty log", "employeeId": 1 }

Product:
GET http://localhost:8080/product/all

GET http://localhost:8080/product/{id}

POST http://localhost:8080/product
{ "name": "new product" }

DELETE http://localhost:8080/product/{id}

PUT http://localhost:8080/product

{ "id": 2, "name": "new product name" }

DELETE http://localhost:8080/product/{id}/deleteEmployee/{employee_id}

PUT http://localhost:8080/product/{id}/addEmployee/{employee_id}

Employee:
GET http://localhost:8080/employee/all

GET http://localhost:8080/employee/{id}

POST http://localhost:8080/employee

{ "firstName": "New firstName", "lastName": "New LastName", "role": { "id": 4 } }

DELETE http://localhost:8080/employee/{id}