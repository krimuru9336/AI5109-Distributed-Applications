# Distributed Applications - WS 2023/2024
This repository contains source code for the **Distributed Applications** module in the Winter Semester 2023/2024 at Hochschule Fulda.

## Environment

The project uses 3 servers: One for the database, one for the backend and one as a Azure Static Web App for the frontend, as well as a key vault to store the database credentials. They are all hosted on Microsoft Azure.

Database and backend run on Ubuntu 22.04 LTS.

## Setting up the Static Web App

In the azure portal, create a new Static Web App. Search for `Static Web App` in the search bar and click on `Create`. Fill in the required information 
```
Plan type: free
Deployment details: other
```
and click on `Review + create`. After the validation is complete, click on `Create`.

## Setting up Database and Backend

Follow the same steps as in Week 4.
In addition, you need to set the URL that is provided by the Azure Static Web App as the `da.bmi.cors.allowed-origin` in the `application.properties` file.

For the Static Web App to be able to communicate with the backend, you need to have HTTPS enabled on your server. (You need a certificate from a valid CA or use a self-signed one - for more information on how to set them up, refer to the README in Week 4.)

## Setting up the frontend

The `frontend` folder contains the source code (and the build) for the frontend. It is a Vue.js project.

In the configuration file `src/config/config.js` you need to set the URL of the backend as the `apiUrl` value.

Go into the `frontend` folder and run 
```bash
npm install
```
to install all dependencies. After that, run
```bash
npm run build
``` 
to build the project. The build files will be in the `dist` folder.

## Deploying the Static Web App

We will be using the `SWA CLI` to deploy the frontend, which requires you to install [Node.js](https://nodejs.org/en). You can install it with

```bash
npm install -g @azure/static-web-apps-cli
```

After that, you can deploy the frontend with

```bash
swa deploy <path_to_build_frontend> --env production --deployment-token <deployment-token>
```
Replace \<path_to_build_frontend\> with the path to the `dist` folder in the `frontend` folder and \<deployment-token\> with the deployment token of the Static Web App. You can find the deployment token in the Static Web App's overview page in the Azure Portal under `Manage deployment token`.

**_NOTE:_** You may need to login into your Azure account with `az login` before you can deploy the frontend. Refer to the [documentation](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) for more information.

## Running the application

After the deployment is complete, you can access the application at the URL of the Static Web App.
Make sure that the backend is running and that the database is accessible from the backend server.