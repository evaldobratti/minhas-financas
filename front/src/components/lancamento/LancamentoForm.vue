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
            <v-date-picker v-model="lancamento.data" :date-format="formatDate"
              :formatted-value.sync="dataLancamentoFormatada" no-title scrollable actions></v-date-picker>
          </v-menu>
      </v-flex>
      <v-flex xs5>
        <LocalAutoComplete :errorMessages="errorMessages('local')" v-model="lancamento.local" label="Local"/>
      </v-flex>
      <v-flex xs5>
        <CategoriaAutoComplete :errorMessages="errorMessages('categoria')" v-model="lancamento.categoria" label="Categoria" />
      </v-flex>
      <v-flex xs1>
        <v-text-field
          ref="valor"
          v-model="lancamento.valor"
          label="Valor"
          type="number"
          class="number-input">
        </v-text-field>
      </v-flex>
      <v-flex xs2>
        <v-checkbox label="Efetuada" v-model="lancamento.efetuada"></v-checkbox>
      </v-flex>
      <v-btn type="submit" v-show="false">wtf</v-btn>
    </v-layout>
  </form>
</template>

<script>
import axios from 'axios';
import { d } from '../../store/categorias';
import { lancamentos } from '../../store/lancamento';
import CategoriaIcone from '../categoria/CategoriaIcone';
import LocalAutoComplete from './LocalAutoComplete';
import CategoriaAutoComplete from './CategoriaAutoComplete';
import { LOCAIS } from '../../store/locais';

export default {
  props: ['conta'],
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS);
    this.$store.dispatch(LOCAIS.d.LOAD_LOCAIS);
  },
  watch: {
    conta(val) {
      this.lancamento.conta = this.conta;
    }
  },
  data() {  
    return {
      menu: false,
      dataLancamentoFormatada: new Date().toLocaleDateString(),
      lancamento: {
        data: new Date(),
        conta: null,
        valor: null,
        categoria: null,
        local: null,
        efetuada: null
      }
    }
  },
  methods: {
    errorMessages(field) {
      return [];
    },
    formatDate(val) {
      this.lancamento.data = new Date(val);
      return new Date(val).toLocaleDateString();
    },
    submit() {
      this.$refs.valor.blur();
      this.$store.dispatch(lancamentos.d.LANCAMENTO_SUBMIT, this.lancamento).then(() => {
      });
    },
    categoriaAsFlat(categoria) {
      let flat = [ categoria ];
      for (let c of categoria.filhas) {
        for (let f of this.categoriaAsFlat(c))
          flat.push(f);
      }
      return flat;
    },
    categoriaDistanciaAteRaiz(categoria) {
      let pai = categoria.pai;
      let nivel = 0;
      while (pai != null) {
        nivel += 1;
        pai = pai.pai;
      }
      return nivel;
    },
  },
  computed: {
    categoriasFlat() {
      let flat = [];
      for (let c of this.categorias) {
        flat = flat.concat(this.categoriaAsFlat(c));
      }
      return flat;
    },
    categorias() {
      return this.$store.state.categorias.list;
    },
  },
  components: {
    CategoriaIcone,
    LocalAutoComplete,
    CategoriaAutoComplete
  }
}
</script>

<style>
  
</style>
