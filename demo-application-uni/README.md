The application is developed using Ruby On Rails, [HomePage for Ruby on Rails](https://rubyonrails.org/)

### About the Setup :


Currently this application uses Puma as its server and we are using html and css for the views of the application.
This application is deployed using DockerHub, Azure App Services
It uses Sql server and a sql database for storing the data.

I have also created a github action that, when I merge to main branch, it creates a docker image and pushes it to Docker Hub with a tag. 

To deploy the application, I deploy it manually right now to production from Azure Web App Services.

# ------- This setup is currently for Mac Users -------

### Pre-Requisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [RVM](https://nrogap.medium.com/install-rvm-in-macos-step-by-step-d3b3c236953b)
- [GIT](https://formulae.brew.sh/formula/git)

To run the application: 
`docker-compose up`

