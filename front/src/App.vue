<template>
  <div id="app">
    <v-app standalone>
      <v-navigation-drawer permanent clipped light v-if="isAuthenticated">
        <v-list dense class="pt-0">
          <v-list-tile>
            <v-list-tile-action>
              <v-icon>dashboard</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>Minhas finanças</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-navigation-drawer>
      <v-toolbar class="teal" dark>
        <v-toolbar-title>Minhas finanças</v-toolbar-title>
      </v-toolbar>
      <main>
        <v-container fluid>
          <router-view></router-view>
        </v-container>
      </main>
    </v-app>
  </div>
</template>

<script>
export default {
  name: 'app',
  computed: {
    isAuthenticated() {
      return this.$store.state.authentication.isAuthenticated;
    }
  }
}
</script>

<style>
</style>
