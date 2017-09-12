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
        <v-text-field 
          ref="localTxt"
          label="Local"
          :error-messages="errorMessages('local')"
          v-model="local"></v-text-field>  
      </v-flex>
      <v-flex xs5>
        <v-select
          label="Categoria"
          :items="categoriasFlat"
          v-model="categoria"
          item-text="nome"
          :error-messages="errorMessages('categoria')"
          :search-input.sync="inputed"
          autocomplete>
          <template slot="item" scope="data">
            <span :style="{'padding-left': 25 * categoriaDistanciaAteRaiz(data.item) + 'px'}">
              <CategoriaIcone :categoria="data.item"></CategoriaIcone>
              <small v-if="data.item.id == null">Incluir</small>
              {{ data.item.nome }}
            </span>
          </template>
        </v-select>
      </v-flex>
      <v-flex xs1>
        <v-text-field
          v-model="valor"
          label="Valor"
          type="number"
          class="number-input">
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
import CategoriaIcone from '../categoria/CategoriaIcone';

export default {
  props: ['conta'],
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS);
  },
  watch: {
    conta(val) {
      this.$store.commit(lancamentos.m.LANCAMENTO_SET_CONTA, this.conta);  
    },
    inputed(val) {
      if (val == null)
        return;
      //this.$store.state.categorias.list = this.$store.state.categorias.list.filter(c => c.id != null);
      
      let possible = this.categoriasFlat.find(c => c.nome.toLowerCase().indexOf(val.toLowerCase()) >= 0);
      if (possible == null) {
        const categoria = this.$store.state.categorias.list.find(c => c.id == null);
        if (categoria) {
          categoria.nome = val;
        } else {
          this.$store.state.categorias.list.push({
            nome: val, 
            filhas: [],
            pai: null
          })
        }
        console.info(this.categoriasFlat[this.categoriasFlat.length-1].nome);
      }
    },
    categoria(val) {
      console.info('atualizado ', val ? val.nome : 'nulo');
    }
  },
  data() {  
    return {
      menu: false,
      dataLancamentoFormatada: new Date().toLocaleDateString(),
      inputed: ''
    }
  },
  methods: {
    errorMessages(field) {
      return this.$store.state.lancamentos.formErrors[field];
    },
    formatDate(val) {
      this.data = new Date(val);
      return new Date(val).toLocaleDateString();
    },
    submit() {
      this.$store.dispatch(lancamentos.d.LANCAMENTO_SUBMIT).then(() => {
        this.$refs.localTxt.focus();
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
  },
  components: {
    CategoriaIcone
  }
}
</script>

<style>
  
</style>
