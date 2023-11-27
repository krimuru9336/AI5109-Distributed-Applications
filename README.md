# AI5109-Distributed-Applications

Project for Distributed applications

## Basic Principles

1. For bmi application access endpoint /index

2. For Calling External api project, you can find details about countries enter the country name after /find endpoint like
/find/{country name}.


This is how the application is distributed

- I have used Mysql flexible database that is deployed in azure as a server
- I have created separate backend and frontend virtual machines
- I have used springboot as the main backend, which will process the logic for bmi and call external apis.
- for frontend i started nginx server that will serve html template files
