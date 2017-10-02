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
              <v-list-tile-title>
                <router-link :to="{ name: 'dashboard' }">
                  Dashboard
                </router-link>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
          <v-list-tile>
            <v-list-tile-action>
              <v-icon>compare_arrows</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>
                <router-link :to="{ name: 'analise' }">
                  Análise conjunta
                </router-link>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
          <v-list-tile v-for="conta in contas" :key="conta.id">
            <v-list-tile-action>
              <v-icon>attach_money</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>
                <router-link :to="{ name: 'detalhe-conta', params: { id: conta.id } }">
                  {{ conta.nome }}
                </router-link>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
          <v-list-tile>
            <v-list-tile-action>
              <v-icon>filter_list</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>
                <router-link :to="{ name: 'categorias' }">
                  Categorias
                </router-link>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
          <v-list-tile>
            <v-list-tile-action>
              <v-icon>file_upload</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>
                <router-link :to="{ name: 'upload' }">
                  Upload
                </router-link>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
        </v-list>
      </v-navigation-drawer>
      <v-toolbar class="teal" dark fixed>
        <v-toolbar-title>Minhas finanças</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-menu bottom right v-if="isAuthenticated">
          <v-btn icon slot="activator" dark>
            <v-icon>more_vert</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile>
              <v-list-tile-title @click="logout">Logout</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
      </v-toolbar>
      <main>
        <v-container fluid>
          <Snackbar></Snackbar>
          <router-view></router-view>
        </v-container>
      </main>
    </v-app>
  </div>
</template>

<script>
import Snackbar from './components/Snackbar'
export default {
  name: 'app',
  methods: {
    logout() {
      this.$store.commit('loggedOut');
      this.$root.$emit('snack', { text: 'vc foi desconectado :)' })
    }
  },
  computed: {
    isAuthenticated() {
      return this.$store.state.authentication.isAuthenticated;
    },
    contas() {
      return this.$store.state.conta.list;
    }
  },
  components: {
    Snackbar
  }
}
</script>

<style>

.small-fab-btn {
  width: 20px !important; 
  height: 20px !important;
  margin: 0 !important;
  box-shadow: none;
}

.number-input input {
  text-align: right;
}

</style>
