<template>
  <div id="app">
    <BmiApp class="bmi-container" />
    <RandomDuck class="duck-img-container"/>
  </div>
</template>

<script>
import BmiApp from './components/BmiApp.vue';
import RandomDuck from './components/RandomDuck.vue';

export default {
  name: 'App',
  components: {
    RandomDuck,
    BmiApp,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
.bmi-container {
  display: flex;
}

</style>
