<template>
  <v-app>
    <v-navigation-drawer app clipped fixed v-if="isAuthenticated">
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
    <v-toolbar app clipped-left class="teal" dark fixed>
      <v-toolbar-title>Minhas finanças</v-toolbar-title>
      <v-spacer />
      <v-menu 
        bottom 
        right 
        v-if="isAuthenticated">
        <v-btn 
          icon 
          slot="activator" 
          dark>
          <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile>
            <v-list-tile-title @click="logout">Logout</v-list-tile-title>
          </v-list-tile>
          <v-list-tile>
            <v-list-tile-title @click="dialogSobre = true">Sobre</v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>
    </v-toolbar>
    <v-content>
      <v-container fluid>
        <Snackbar />
        <Migrating v-if="isMigrating" />
        <router-view v-if="!isMigrating" :key="$route.path" />
      </v-container>
    </v-content>
    <v-dialog v-model="dialogSobre" width="300px">
      <v-card>
          <v-card-title>
            <div class="headline">Sobre</div>
          </v-card-title>
        <v-card-text>
          <About />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script>
import Snackbar from './components/Snackbar';
import Migrating from './components/Migrating'
import AUTH from './store/auth';
import About from './components/About';

export default {
  name: 'app',
  data() {
    return {
      dialogSobre: false
    }
  },
  methods: {
    logout() {
      this.$store.dispatch(AUTH.d.LOGOUT);
    }
  },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    isMigrating() {
      return this.$store.getters.isMigrating;
    },
    contas() {
      return this.$store.state.conta.asList;
    }
  },
  components: {
    Snackbar,
    Migrating,
    About
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

/*main .content {
  padding: 64px 0px 0px 200px !important;
}*/

</style>
