<template>
  <form @submit.prevent="submit">
        <v-text-field :disabled="recorrencia.id != null"
          label="A cada"
          v-model="recorrencia.aCada"></v-text-field>
        <v-select
          :disabled="recorrencia.id != null"
          label="Frequência"
          :items="frequencias"
          item-text="text"
          item-value="value"
          v-model="recorrencia.tipoFrequencia"></v-select>
        <v-select
          :disabled="recorrencia.id != null"
          label="Conta"
          :items="contas"
          item-text="nome"
          item-value="value"
          v-model="recorrencia.conta"></v-select>
        <v-text-field
          :disabled="recorrencia.id != null"
          label="Valor"
          v-model="recorrencia.valor"></v-text-field>
      <v-layout>
        <v-flex xs6>
          <DatePicker label="Inicio" :disabled="recorrencia.id != null" v-model="recorrencia.partirDe"></DatePicker>
        </v-flex>
        <v-flex xs6>
          <DatePicker label="Fim" :disabled="(recorrencia.id != null && recorrencia.dataFim != null) && submetido" v-model="recorrencia.dataFim"></DatePicker>
        </v-flex>
      </v-layout>
      <v-layout justify-space-around>
        <v-btn type="submit">Salvar</v-btn>
      </v-layout>
  </form>
</template>

<script>
import { RECORRENCIA } from '../../store/recorrencia';
import mapGetSet from '../../store/mapGetSet';
import DatePicker from '../DatePicker';
import { lancamentos } from '../../store/lancamento';
import { SNACKS } from '../../store/snacks';
import axios from 'axios';

export default {
  components: {
    DatePicker
  },
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
      }],
      recorrencia: {
        id: null,
        valor: null,
        partirDe: null,
        conta: null,
        local: null,
        categoria: null,
        dia: null,
        lancamentoInicial: null,
        dataFim: null
      },
      submetido: false
    }
  },
  mounted() {
    this.updateLancamento(this.lancamento);
  },
  watch: {
    lancamento(lancamento) {
      this.updateLancamento(lancamento);
    }
  },
  computed: {
    contas() {
      return this.$store.state.conta.list;
    }
  },
  methods: {
    updateLancamento(l) {
      if (!l)
        return;

      this.submetido = false;

      if (l.motivo && l.motivo['@class'].endsWith('RecorrenciaLancamentoGerado')) {
        this.recorrencia = Object.assign({}, l.motivo.recorrencia);  
      }
      else {
        this.$set(this.recorrencia, 'id', null);
        this.$set(this.recorrencia, 'valor', l.valor);
        this.$set(this.recorrencia, 'partirDe', l.data);
        this.$set(this.recorrencia, 'conta', l.conta);
        this.$set(this.recorrencia, 'tipoFrequencia', 'MES');
        this.$set(this.recorrencia, 'aCada', 1);
        this.$set(this.recorrencia, 'local', l.local);
        this.$set(this.recorrencia, 'categoria', l.categoria);
        this.$set(this.recorrencia, 'dia', l.dia);
        this.$set(this.recorrencia, 'lancamentoInicial', l);
        this.$set(this.recorrencia, 'dataFim', null);
      }
    },
    submit() {
      axios.put('/api/recorrencias', this.recorrencia).then(() => {
        this.submetido = true;
        this.$store.dispatch(lancamentos.d.LANCAMENTO_LOAD);
        this.$store.commit(SNACKS.m.UPDATE_SNACK, {
          text: 'Recorrência cadastrada!',
          timeout: 1500,
          context: 'success'
        });
        this.$emit('cadastrado');
      }).catch(err => {
        this.$store.commit(SNACKS.m.TRATA_ERRO, err);
      });
    }
  }
}
</script>

<style>

</style>
