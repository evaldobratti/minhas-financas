<template>
  <form @submit.prevent="submit">
    <v-layout row>
      <v-flex xs2>
        <DatePicker v-model="lancamento.data"></DatePicker>
      </v-flex>
      <v-flex xs5>
        <LocalAutoComplete ref="localAutoComplete" :errorMessages="errorMessages('local')" v-model="lancamento.local" label="Local"/>
      </v-flex>
      <v-flex xs5>
        <CategoriaAutoComplete :errorMessages="errorMessages('categoria')" v-model="lancamento.idCategoria" label="Categoria" />
      </v-flex>
      <v-flex xs1>
        <v-text-field
          ref="valor"
          v-model.number="lancamento.valor"
          label="Valor"
          type="number"
          step="0.01"
          class="number-input">
        </v-text-field>
      </v-flex>
      <v-flex xs2>
        <v-checkbox label="Efetivada" v-model="lancamento.efetivada"></v-checkbox>
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
import DatePicker from '../DatePicker';
import moment from 'moment';
import { SNACKS } from '../../store/snacks';

export default {
  props: {
    idConta: String,
    lancamento: {
      type: Object,
      default() {
        return {
          data: moment(),
          efetivada: true
        };
      }
    }
  },
  created() {
    this.lancamento.idConta = this.idConta;
  },
  watch: {
    idConta(val) {
      this.lancamento.idConta = val;
    },
    'lancamento.data'(data, anterior) {
      if (!data.isSame(anterior)) {
        this.lancamento.ordem = null;
      }
    }
  },
  data() {  
    return {
      menu: false,
    }
  },
  methods: {
    errorMessages(field) {
      return [];
    },
    lancamentoVazio() {
      return {
          valor: null,
          categoria: null,
          local: null,
          efetivada: null
        }
    },
    submit() {
      this.$refs.valor.blur();      
      this.$store.dispatch(lancamentos.d.LANCAMENTO_SUBMIT, this.lancamento).then((msg) => {
        this.$store.commit(SNACKS.m.UPDATE_SUCESSO, msg)

        if (this.lancamento.id == null) {
          this.lancamento.local = '';
          this.lancamento.idCategoria = null;
          this.lancamento.valor = null;
          this.lancamento.ordem = null;
          setTimeout(()  => {
            this.$refs.localAutoComplete.focus()
          }, 100);
        }
        this.$emit('submetido');
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
    refresh() {
      this.$refs.localAutoComplete.refresh();
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
    CategoriaAutoComplete,
    DatePicker
  }
}
</script>

<style>
  
</style>
