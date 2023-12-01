<script>
export default {
  data() {
    return {
      formData: {
        name: '',
        height: null,
        weight: null,
        bmi: null,
      },
      entries: [], // Array to store retrieved entries from the API
    };
  },
  methods: {
    submitForm() {
      // Assume you have a function to send data to the REST API for storage
      // This is just a placeholder, and you need to replace it with your actual API call
      this.storeData(this.formData)
          .then(() => {
            // After successful storage, fetch the updated data from the API
            this.fetchData();
          })
          .catch(error => {
            console.error('Error storing data:', error);
          });
    },
    storeData(data) {
      // Replace this with your actual API endpoint for storing data
      const apiUrl = 'https://webapp-231201185812.azurewebsites.net/Bmi';
      return fetch(apiUrl, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
    },
    fetchData() {
      // Replace this with your actual API endpoint for fetching data
      const apiUrl = 'https://webapp-231201185812.azurewebsites.net/Bmi';
      fetch(apiUrl)
          .then(response => response.json())
          .then(data => {
            this.entries = data;
          })
          .catch(error => {
            console.error('Error fetching data:', error);
          });
    },
  },
  mounted() {
    // Fetch initial data when the component is mounted
    this.fetchData();
  },
};
</script>


<template>
  <div class="bmi-app-container">
    <div>
    <h2>Enter Information</h2>
    <form @submit.prevent="submitForm">
      <label for="name">Name:</label>
      <input type="text" v-model="formData.name" required />

      <label for="height">Height (cm):</label>
      <input type="number" step="0.01" v-model="formData.height" required />

      <label for="weight">Weight (kg):</label>
      <input type="number" step="0.01" v-model="formData.weight" required />

      <button type="submit">Submit</button>
    </form>
    </div>
<div>
  <h2>Stored Information</h2>
    <table>

      <thead>
      <tr>
        <th>Name</th>
        <th>Height (cm)</th>
        <th>Weight (kg)</th>
        <th>BMI</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="entry in entries" :key="entry.id">
        <td>{{ entry.name }}</td>
        <td>{{ entry.height }}</td>
        <td>{{ entry.weight }}</td>
        <td>{{ entry.bmi }}</td>
      </tr>
      </tbody>
    </table>
  </div>
  </div>
</template>


<style scoped>
.bmi-app-container {
  flex: 1; /* Fill the remaining space */
  padding: 10px; /* Add some padding for better readability */
  margin: 10px;
}

h2 {
  font-size: 1.5em;
  margin-bottom: 10px;
}

form {
  margin-bottom: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}

</style>