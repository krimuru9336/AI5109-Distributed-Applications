<template>
  <div class="random-duck-container">
    <img v-if="imageUrl" :src="imageUrl" alt="Random Duck" />
    <div v-else>Loading...</div>
    <div v-if="error">Error: {{ error }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageUrl: null,
      error: null,
    };
  },
  mounted() {
    this.fetchRandomDuck();
  },
  methods: {
    async fetchRandomDuck() {
      try {
        const response = await fetch('https://webapp-231201185812.azurewebsites.net/duckpic',{
          headers: {accept: 'application/json',}
            }
        );

        if (!response.ok) {
          throw new Error(`Failed to fetch: ${response.statusText}`);
        }

        const data = await response.json();

        console.log('API response:', data);

        if (data.url) {
          this.imageUrl = data.url;
        } else {
          throw new Error('Invalid response from the API');
        }
      } catch (error) {
        console.error('Error fetching data:', error);
        this.error = error.message || 'An error occurred';
      }
    },
  },
};
</script>

<style scoped>
.random-duck-container {
  position: fixed;
  top: 0;
  right: 0;
  max-width: 200px;
  max-height: 200px;
  margin: 10px; /* Add margin for spacing */
}

.random-duck-container img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* Maintain aspect ratio and cover the container */
}
</style>