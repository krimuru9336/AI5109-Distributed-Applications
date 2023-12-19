# Use the official Ruby 2.7.0 image
FROM ruby:2.7.0

# Set environment variables
ENV RAILS_ROOT /var/www/app_name
ENV RAILS_ENV production
ENV NODE_ENV production
ENV RAILS_LOG_TO_STDOUT true
ENV RAILS_SERVE_STATIC_FILES true

ARG RAILS_MASTER_KEY
RUN export RAILS_MASTER_KEY=${RAILS_MASTER_KEY}

# Set up the application directory
RUN mkdir -p $RAILS_ROOT
WORKDIR $RAILS_ROOT

# Install dependencies
RUN apt-get update -qq && apt-get install -y nodejs yarn
RUN apt-get install freetds-dev -y

# Copy Gemfile and install gems
COPY Gemfile Gemfile.lock ./
RUN gem install bundler && bundle install --jobs 20 --retry 5


# Copy the rest of the application code
COPY . .
RUN echo $RAILS_MASTER_KEY > /var/www/app_name/config/master.key
# Expose port 3000 to the Docker host, so we can access the app
EXPOSE 8000

# Start the Rails server
CMD ["rails", "server", "-b", "0.0.0.0"]
