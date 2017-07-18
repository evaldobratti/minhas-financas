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
            <v-date-picker v-model="data" :date-format="formatDate"
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
          item-value="id">
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
import { lancamentos } from '../../store/lancamento';

export default {
  props: ['conta'],
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS);
  },
  watch: {
    conta(val) {
      this.$store.commit(lancamentos.m.LANCAMENTO_SET_CONTA, this.conta);  
    }
  },
  data() {
    return {
      menu: false,
      dataLancamentoFormatada: new Date().toLocaleDateString()
    }
  },
  methods: {
    formatDate(val) {
      this.data = new Date(val);
      return new Date(val).toLocaleDateString();
    },
    submit() {
      axios.post('/api/lancamentos', {
        data: this.data,
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
  computed: {
    categorias() {
      return this.$store.state.categorias.list;
    },
    data: {
      get() { return this.$store.state.lancamentos.form.data},
      set(data) { this.$store.commit(lancamentos.m.LANCAMENTO_SET_DATA, data); }
    },
    valor: {
      get() { return this.$store.state.lancamentos.form.valor},
      set(valor) { this.$store.commit(lancamentos.m.LANCAMENTO_SET_VALOR, valor); }
    },
    categoria: {
      get() { return this.$store.state.lancamentos.form.categoria},
      set(categoria) { this.$store.commit(lancamentos.m.LANCAMENTO_SET_CATEGORIA, categoria); }
    },
    local: {
      get() { return this.$store.state.lancamentos.form.local},
      set(local) { this.$store.commit(lancamentos.m.LANCAMENTO_SET_LOCAL, local); }
    },
    efetuada: {
      get() { return this.$store.state.lancamentos.form.efetuada},
      set(efetuada) { this.$store.commit(lancamentos.m.LANCAMENTO_SET_EFETUADA, efetuada); }
    }
  }
}
</script>

<style>
  
</style>
