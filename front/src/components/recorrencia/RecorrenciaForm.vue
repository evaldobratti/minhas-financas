<template>
  <form @submit.prevent="submit">
        <v-text-field
          label="A cada"
          v-model="aCada"></v-text-field>
        <v-select
          label="Frequência"
          :items="frequencias"
          item-text="text"
          item-value="value"
          v-model="tipoFrequencia"></v-select>
        <v-text-field
          label="Valor"
          v-model="valor"></v-text-field>

        <DatePicker v-model="partirDe"></DatePicker>
      <v-layout justify-space-around>
        <v-btn type="submit">Salvar</v-btn>
      </v-layout>
  </form>
</template>

<script>
import { RECORRENCIA } from '../../store/recorrencia';
import mapGetSet from '../../store/mapGetSet';
import DatePicker from '../DatePicker';

export default {
  props: ['lancamento'],
  components: {
    DatePicker
  },
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
  methods: {
    updateLancamento(l) {
      if (!l)
        return;

      this.valor = l.valor;
      this.partirDe = l.data;
      this.conta = l.conta;
      this.local = l.local;
      this.categoria = l.categoria;
      this.dia = l.data.date();
      this.lancamentoInicial = l;
    },
    submit() {
      this.lancamentoInicial = this.lancamento;
      this.$store.dispatch(RECORRENCIA.d.SUBMIT_FORM).then(() => {
        this.$emit('cadastrado');
      });
    }
  },
  computed: {
    ...mapGetSet({
      tipoFrequencia: ['recorrencias.form.tipoFrequencia', RECORRENCIA.m.UPDATE_FORM_TIPO_FREQUENCIA],
      valor: ['recorrencias.form.valor', RECORRENCIA.m.UPDATE_FORM_VALOR],
      aCada: ['recorrencias.form.aCada', RECORRENCIA.m.UPDATE_FORM_A_CADA],
      dia: ['recorrencias.form.dia', RECORRENCIA.m.UPDATE_FORM_DIA],
      partirDe: ['recorrencias.form.partirDe', RECORRENCIA.m.UPDATE_FORM_PARTIR_DE],
      conta: ['recorrencias.form.conta', RECORRENCIA.m.UPDATE_FORM_CONTA],
      categoria: ['recorrencias.form.categoria', RECORRENCIA.m.UPDATE_FORM_CATEGORIA],
      local: ['recorrencia.form.local', RECORRENCIA.m.UPDATE_FORM_LOCAL],
      lancamentoInicial: ['recorrencia.form.lancamentoInicial', RECORRENCIA.m.UPDATE_FORM_LANCAMENTO_INICIAL]
    }),
  }
}
</script>

<style>

</style>
