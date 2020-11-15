# parallel-processing
Spring Boot app for parallel processing of scheduled tasks.  

Tasks implemented using reactive streams (project reactor).  
Spring Data R2DBC for data access, with H2.  
Flyway for db-migrations.  
Shedlock for distributed locks for running tasks on one app instance only, if multiple available.  
