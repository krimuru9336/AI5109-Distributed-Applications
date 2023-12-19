class CreateUsers < ActiveRecord::Migration[6.0]
  def change
    create_table :users do |t|
      t.string :name
      t.string :phone_number

      t.timestamps
    end
  end
end

# Rahil Dutta
# Created 5th November 2023
# Matriculation Number : 1360929