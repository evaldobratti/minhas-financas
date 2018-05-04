<template>
  <form @submit.prevent="submit">
    <v-layout row>
      <v-flex xs2>
        <date-picker v-model="data" />
      </v-flex>
      <v-flex xs3>
        <v-select
          label="Conta origem"
          v-bind:items="contas"
          v-model="idContaOrigem"
          item-text="nome"
          item-value="id"
          max-height="auto">
        </v-select>
      </v-flex>
      <v-flex xs3>
        <v-text-field
          ref="valor"
          v-model.number="valorOrigem"
          label="Valor origem"
          type="number"
          step="0.01"
          class="number-input" 
          @input="valorOrigemChanged"/>
      </v-flex>
      <v-flex xs3>
        <v-select
        label="Conta destino"
        v-bind:items="contas"
        v-model="idContaDestino"
        item-text="nome"
        item-value="id"
        max-height="auto">
      </v-select>
      </v-flex>
      <v-flex xs3>
        <v-text-field
          ref="valor"
          v-model.number="valorDestino"
          label="Valor destino"
          type="number"
          step="0.01"
          class="number-input"
          @input="valorDestinoChanged" />
      </v-flex>
      <v-flex xs2>
        <v-checkbox label="Efetivada" v-model="efetivada"></v-checkbox>
      </v-flex>
      <v-btn type="submit" v-show="false">wtf</v-btn>
    </v-layout>
  </form>
</template>

<script>
import moment from 'moment';
import CONTA from '../../store/conta';
import DatePicker from '../DatePicker';
import TRANSFERENCIAS from '../../store/transferencias';
import { SNACKS } from "../../store/snacks";

export default {
  props: ['idContaOrigem'],
  data() {
    return {
      data: moment(),
      valorOrigem: null,
      idContaDestino: null,
      valorDestino: null,
      efetivada: false
    }
  },
  methods: {
    valorOrigemChanged(value) {
      this.valorDestino = -Number(value)
    },
    valorDestinoChanged(value) {
      this.valorOrigem = -Number(value)
    },
    submit() {
      this.$store.dispatch(TRANSFERENCIAS.d.SUBMIT, {
        data: this.data,
        idContaOrigem: this.idContaOrigem,
        idContaDestino: this.idContaDestino,
        valor: this.valorOrigem,
      }).then((msg) => {
        this.$store.commit(SNACKS.m.UPDATE_SUCESSO, msg);
      })
    }
  },
  computed: {
    contas() {
      return this.$store.getters.getContas;
    }
  },
  components: {
    DatePicker
  }
}
</script>

<style>
  
</style>
