<template>
  <v-data-table
    :headers="headers"
    :items="lancamentosComSaldo"
    class="elevation-1"
    hide-actions
  >
    <template slot="items" slot-scope="props">
      <tr>
        <td>
          {{ props.item.data && props.item.data.format('DD/MM/YYYY') }}
        </td>
        <td>{{ props.item.descricao }}</td>
        <td>{{ props.item.valor }}</td>
        <td>{{ props.item.saldo }}</td>
        <td><v-checkbox v-if="props.item.id" @change="$emit('alternaEfetiva', props.item)" v-model="props.item.efetivada" /></td>
        <td><v-btn v-if="props.item.id" class="error" @click="$emit('excluir', props.item)">Excluir</v-btn></td>
        
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
          text: 'Data'
        },
        {
          text: 'Descrição'
        },
        {
          text: 'Valor'
        },
        {
          text: 'Saldo'
        },
        {
          text: 'Efetivada'
        },
        {
          text: 'Ações'
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
  }

}
</script>

<style>

</style>
