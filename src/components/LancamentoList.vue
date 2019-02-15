<template>
  <v-data-table
    :headers="headers"
    :items="lancamentosComSaldo"
    class="elevation-1"
    hide-actions
  >
    <template slot="items" slot-scope="props">
      <tr :key="props.item.id">
        <td>
          {{ props.item.data && props.item.data.format('DD/MM/YYYY') }}
        </td>
        <td>{{ props.item.descricao }}</td>
        <td><currency :value="props.item.valor" /></td>
        <td><currency :value="props.item.saldo" /></td>
        <td><v-checkbox v-if="props.item.id" @change="$emit('alternaEfetiva', props.item)" v-model="props.item.efetivada" /></td>
        <td>
          <v-btn v-if="props.item.id" @click="$emit('subir', props.item)" :disabled="props.item.lancamentoAnterior == null">Subir</v-btn>
          <v-btn v-if="props.item.id" @click="$emit('descer', props.item)" :disabled="temLancamentoPosterior(props.item) == null">Descer</v-btn>
          <v-btn v-if="props.item.id" class="error" @click="$emit('excluir', props.item)">Excluir</v-btn>
        </td>
        
      </tr>
    </template>
  </v-data-table>
</template>

<script>
import _ from 'underscore'

export default {
  props: ['lancamentos', 'saldos'],
  data() {
    return {
      headers: [
        {
          text: 'Data',
          sortable:false
        },
        {
          text: 'Descrição',
          sortable:false
        },
        {
          text: 'Valor',
          sortable:false
        },
        {
          text: 'Saldo',
          sortable:false
        },
        {
          text: 'Efetivada',
          sortable:false
        },
        {
          text: 'Ações',
          sortable:false
        },
      ]
    }
  },
  computed: {
    lancamentosComSaldo() {
      return [
        {
          descricao: 'Saldo anterior',
          saldo: this.saldos[0],
        },
        ...this.lancamentos.map((l, ix) => ({...l, saldo: this.saldos[ix + 1]})),
        {
          descricao: 'Saldo posterior',
          saldo: _.last(this.saldos)
        }
      ]
    }
  },
  methods: {
    temLancamentoPosterior(lancamento) {
      return this.$store.getters.lancamentoPosterior(lancamento)
    }
  }

}
</script>

<style>

</style>
