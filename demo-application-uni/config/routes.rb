Rails.application.routes.draw do
  resources :users # creates the REST API routes
  # For details on the DSL available within this file, see https://guides.rubyonrails.org/routing.html
  get '/sample_api', to: 'users#sample_api'
  root 'users#index'
end

# Rahil Dutta
# Created 5th November 2023
# Matriculation Number : 1360929