<template>
  <v-app id="inspire">
    <v-navigation-drawer
      v-model="drawer"
      clipped
      fixed
      app
    >
      <v-list dense>
        <v-list-tile>
          <v-list-tile-action>
            <v-icon>dashboard</v-icon>
          </v-list-tile-action>
          <router-link to="/about">
            <v-list-tile-content>
              <v-list-tile-title>Dashboard</v-list-tile-title>
            </v-list-tile-content>
          </router-link>
        </v-list-tile>
        
        <v-list-tile @click="dialogNovaConta = true">
          <v-list-tile-action>
            <v-icon>add</v-icon>
          </v-list-tile-action>
          <v-list-tile-title>Nova conta</v-list-tile-title>
        </v-list-tile>
        <v-list-tile v-for="conta in contas" :key="conta.id" :to="'/contas/' + conta.id">
          <v-list-tile-action>
            <v-icon>settings</v-icon>
          </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{conta.nome}}</v-list-tile-title>
            </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar app fixed clipped-left>
      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title>Application</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-menu bottom left>
          <v-btn
            slot="activator"
            icon
          >
            <v-icon>more_vert</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile @click="logout">
              <v-list-tile-title>Sair</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <v-layout>
          <v-flex shrink>
            <router-view></router-view>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <v-footer app fixed>
      <span>&copy; 2017</span>
    </v-footer>
    <v-dialog
      v-model="dialogNovaConta"
      max-width="290"
    >
      <v-card>
        <v-card-title class="headline">Nova conta</v-card-title>

        <v-card-text>
          <conta-form @salvo="dialogNovaConta = false" />  
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script>
import ContaForm from '../components/ContaForm'

export default {
  data: () => ({
    drawer: null,
    dialogNovaConta: false
  }),
  methods: {
    logout() {
      this.$store.dispatch('logout')
    }
  },
  computed: {
    contas() {
      return this.$store.state.contas.list
    }
  },
  props: {
    source: String
  },
  components: {
    ContaForm
  }
}
</script>

<style>

</style>
