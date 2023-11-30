<template>
  <div>
    <div id="error" v-if="error">
      <v-alert type="error" >{{ error }}</v-alert>
    </div>
    <div id="message" v-if="formSubmitted">
      <p>Hello {{ message.name }}</p>
      <p>Your BMI is: {{ message.bmi }}</p>
    </div>

    <section id="bmiForm">
      <v-form @submit.prevent="submitForm">
        <v-text-field v-model="newEntry.name" label="Name"></v-text-field>
        <v-text-field v-model="newEntry.height" label="Height"></v-text-field>
        <v-text-field v-model="newEntry.weight" label="Weight"></v-text-field>
        <v-btn type="submit" color="primary">Submit</v-btn>
      </v-form>
    </section>

    <section id="bmiTable">
      <v-data-table v-if="entries && entries.length > 0" :items="entries" :headers="headers"></v-data-table>
    </section>
  </div>
</template>

<script lang="ts">
import config from "@/config/config"; 

export default {
  data() {
    return {
      path: "/api/entries",
      entries: [] as Array<{ id: number; name: string; height: number; weight: number; bmi: number }>,
      newEntry: {
        name: "",
        height: 0,
        weight: 0,
        bmi: 0,
      },
      error: "",
      formSubmitted: false,
      headers: [
        { text: "ID", value: "id" },
        { text: "Name", value: "name" },
        { text: "Height", value: "height" },
        { text: "Weight", value: "weight" },
        { text: "BMI", value: "bmi" },
      ],
      message: {
        name: "",
        bmi: 0,
      },
    };
  },

  methods: {
    submitForm() {
      this.addEntry(this.newEntry)
        .then((response) => {
          console.log("response:", response);
          this.entries.push(response);

          this.formSubmitted = true;

          this.message.name = response.name;
          this.message.bmi = response.bmi;

          // Reset the form
          this.newEntry = { name: "", height: 0, weight: 0, bmi: 0 };
        })
        .catch((error) => {
          console.error("Error adding entry:", error);
        });
    },
    async fetchEntries() {
      try {
        const response = await fetch(config.apiUrl + this.path);
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      } catch (error) {
        console.error("Error fetching entries:", error);
        this.error = "Server not reachable.";
        throw error;
      }
    },

    async addEntry(data: any) {
      try {
        const response = await fetch(config.apiUrl + this.path, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        });

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return response.json();
      } catch (error) {
        console.error("Error adding entry:", error);
        throw error;
      }
    },
  },
  mounted() {
    this.fetchEntries()
      .then((response) => {
        this.entries = response;
      })
      .catch((error) => {
        console.error("Error fetching entries:", error);
      });
  },
};
</script>

<style scoped>
#bmiForm, #bmiTable, #message, #error {
  margin: 20px;
}
</style>