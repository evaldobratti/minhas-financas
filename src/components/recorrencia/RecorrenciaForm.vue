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
          item-value="id"
          v-model="recorrencia.idConta"></v-select>
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
      <v-layout>
        <v-flex xs6>
          <v-text-field
            v-model.number="recorrencia.parcelaInicio"
            label="Parcela">
          </v-text-field>
          <v-text-field
            v-model.number="recorrencia.parcelaQuantidade"
            label="de">
          </v-text-field>
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
      recorrencia: {},
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
      return this.$store.getters.getContas;
    }
  },
  methods: {
    updateLancamento(l) {
      if (!l)
        return;

      this.submetido = false;

      let recorrenciaOriginadora = this.$store.getters.recorrenciaOriginadora(l);
      
      if (!recorrenciaOriginadora)
        recorrenciaOriginadora = this.$store.getters.getRecorrencia(l);

      if (recorrenciaOriginadora) {
        this.recorrencia = Object.assign({}, recorrenciaOriginadora);  
      } else {
        this.$set(this.recorrencia, 'id', null);
        this.$set(this.recorrencia, 'valor', l.valor);
        this.$set(this.recorrencia, 'partirDe', l.data);
        this.$set(this.recorrencia, 'idConta', l.idConta);
        this.$set(this.recorrencia, 'tipoFrequencia', 'MES');
        this.$set(this.recorrencia, 'aCada', 1);
        this.$set(this.recorrencia, 'local', l.local);
        this.$set(this.recorrencia, 'idCategoria', l.idCategoria);
        this.$set(this.recorrencia, 'dataFim', null); 
        this.$set(this.recorrencia, 'lancamentos', []);
      }
    },
    submit() {
      
      const parcelaGerada = {
        data: this.recorrencia.partirDe,
        idLancamento: this.lancamento.id
      };

      if (this.recorrencia.parcelaInicio) {
        parcelaGerada.parcelaNumero = this.recorrencia.parcelaInicio;
      }

      this.$store.dispatch(RECORRENCIA.d.SUBMIT_FORM, { recorrencia: this.recorrencia, gerada: parcelaGerada }).then(() => {
        this.$emit('cadastrado'); 
      });
    }
  }
}
</script>

<style>

</style>
