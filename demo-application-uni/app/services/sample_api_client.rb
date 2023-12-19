require 'net/http'
require 'json'

class SampleApiClient
  BASE_URL = 'https://jsonplaceholder.typicode.com' # base URL of API

  def get_user(user_id = 1) # making default user id to 1
    uri = URI("#{BASE_URL}/users/#{user_id}")
    
    response = Net::HTTP.get_response(uri) # creates the headers and calls the url

    if response.is_a?(Net::HTTPSuccess)
      parse_response(response)
    else
      puts "Error: #{response.code} - #{response.message}"
    end
  end

  def parse_response(response)
    user_data = JSON.parse(response.body)
    {
      id: user_data['id'],
      username: user_data['username']
    }
  end
end
# Rahil Dutta
# Created 16th November 2023
# Matriculation Number : 1360929