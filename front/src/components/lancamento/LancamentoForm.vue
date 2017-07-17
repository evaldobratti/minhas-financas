<template>
  <form @submit.prevent="submit">
    <v-layout row>
      <v-flex xs2>
        <v-menu
            lazy
            :close-on-content-click="true"
            v-model="menu"
            offset-y
            full-width
            :nudge-left="40"
            max-width="290px"
          >
            <v-text-field
              slot="activator"
              label="Data"
              v-model="dataLancamentoFormatada"
              prepend-icon="event"
              readonly
            ></v-text-field>
            <v-date-picker v-model="dataLancamento" :date-format="formatDate"
              :formatted-value.sync="dataLancamentoFormatada" no-title scrollable actions></v-date-picker>
          </v-menu>
      </v-flex>
      <v-flex xs5>
        <v-text-field label="Local"
          v-model="local"></v-text-field>  
      </v-flex>
      <v-flex xs5>
        <v-select
          label="Categoria"
          :items="categorias"
          v-model="categoria"
          item-text="nome"
          item-value="value"
          autocomplete
          editable>
        </v-select>
      </v-flex>
      <v-flex xs1>
        <v-text-field
          v-model="valor"
          label="Valor">
        </v-text-field>
      </v-flex>
      <v-flex xs2>
        <v-checkbox label="Efetuada" v-model="efetuada"></v-checkbox>
      </v-flex>
      <v-btn type="submit" v-show="false">wtf</v-btn>
    </v-layout>
  </form>
</template>

<script>
import axios from 'axios';
import { d } from '../../store/categorias';

export default {
  props: ['conta'],
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS)
      .then((categorias) => this.categorias = categorias);
  },
  data() {
    return {
      categorias: [],
      menu: false,
      dataLancamento: new Date(),
      dataLancamentoFormatada: new Date().toLocaleDateString(),
      valor: 0,
      categoria: 0,
      local: '',
      efetuada: false

    }
  },
  methods: {
    formatDate(val) {
      this.dataLancamento = new Date(val);
      return new Date(val).toLocaleDateString();
    },
    submit() {
      axios.post('/api/lancamentos', {
        data: this.dataLancamento,
        conta: this.conta.id,
        valor: this.valor,
        categoria: this.categoria,
        local: this.local,
        efetivada: this.efetuada
      }).then(res => {
        console.info(res);
      }).catch(err => {
        console.error(err);
      })
    }
  },
}
</script>

<style>
  
</style>
