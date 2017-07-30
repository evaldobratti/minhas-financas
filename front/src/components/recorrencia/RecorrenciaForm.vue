<template>
  <form @submit.prevent="submit" style="min-width: 600px">
    <v-layout row>
      <v-flex xs2>
        <v-text-field 
          label="A cada"
          v-model="aCada"></v-text-field>  
      </v-flex>
      <v-flex xs5>
        <v-select
          label="Frequência"
          :items="frequencias"
          item-text="text"
          item-value="value"
          v-model="tipoFrequencia"></v-select>  
      </v-flex>
    </v-layout>
    <v-layout row>
      <v-flex xs2>
        <v-text-field 
          label="Valor"
          v-model="valor"></v-text-field>  
      </v-flex>
      <v-flex xs2>
        <v-text-field 
          label="A partir de"
          v-model="partirDe"></v-text-field>  
      </v-flex>
      
    </v-layout>
    <v-layout row>
      <v-flex xs5>
        <v-btn type="submit">Salvar</v-btn>
      </v-flex>
    </v-layout>
  </form>
</template>

<script>
import { RECORRENCIA } from '../../store/recorrencia';

export default {
  props: ['lancamento'],
  data() {
    return {
      frequencias: [{
        value: 'DIA',
        text: 'Dia'
      }, {
        value: 'MES',
        text: 'Mês'
      }, {
        value: 'ANO',
        text: 'Ano'
      }]
    }
  },
  mounted() {
    this.updateLancamento(this.lancamento);
  },
  watch: {
    lancamento(l) {
      this.updateLancamento(l);
    }
  },
  methods: {
    updateLancamento(l) {
      this.valor = l.valor;
      this.partirDe = l.data;
      this.conta = l.conta;
      this.local = l.local;
      this.categoria = l.categoria;
      this.dia = l.data.date();
    },
    submit() {
      this.$store.dispatch(RECORRENCIA.d.SUBMIT_FORM);
    }
  },
  computed: {
    tipoFrequencia: {
      get() {
        return this.$store.state.recorrencias.form.tipoFrequencia;
      },
      set(tipoFrequencia) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { tipoFrequencia });
      }
    },
    aCada: {
      get() {
        return this.$store.state.recorrencias.form.aCada;
      },
      set(aCada) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { aCada });
      }
    },
    valor: {
      get() {
        return this.$store.state.recorrencias.form.valor;
      },
      set(valor) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { valor });
      }
    },
    dia: {
      get() {
        return this.$store.state.recorrencias.form.dia;
      },
      set(dia) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { dia });
      }
    },
    partirDe: {
      get() {
        return this.$store.state.recorrencias.form.partirDe;
      },
      set(partirDe) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { partirDe });
      }
    },
    conta: {
      get() {
        return this.$store.state.recorrencias.form.conta;
      },
      set(conta) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { conta });
      }
    },
    categoria: {
      get() {
        return this.$store.state.recorrencias.form.categoria;
      },
      set(categoria) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { categoria });
      }
    },
    local: {
      get() {
        return this.$store.state.recorrencias.form.local;
      },
      set(local) {
        this.$store.commit(RECORRENCIA.m.UPDATE_FORM, { local });
      }
    },
  }
}
</script>

<style>
  
</style>
