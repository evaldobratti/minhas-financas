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
        <td><v-checkbox v-if="props.item.id" @change="$emit('alternaEfetiva', props.item.id)" v-model="props.item.efetivada" /></td>
        <td>
          <v-btn fab small  v-if="props.item.id" @click="$emit('subir', props.item.id)" :disabled="props.item.lancamentoAnterior == null"><v-icon>expand_less</v-icon></v-btn>
          <v-btn fab small  v-if="props.item.id" @click="$emit('descer', props.item.id)" :disabled="temLancamentoPosterior(props.item) == null"><v-icon>expand_more</v-icon></v-btn>
          <v-btn fab small  v-if="props.item.id" class="error" @click="$emit('excluir', props.item.id)"><v-icon>delete</v-icon></v-btn>
          <v-btn fab small v-if="props.item.id" @click="$emit('editar', props.item.id)"><v-icon>edit</v-icon></v-btn>
        </td>
        
      </tr>
    </template>
  </v-data-table>
</template>

<script>
import _ from 'underscore'
import { isNullOrUndefined } from 'util';

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
        ...this.lancamentos.map((l, ix) => ({
          ...l, 
          saldo: this.saldos[ix + 1],
          descricao: l.idContaDestino ? 'Transferência para ' + this.$store.getters.contaId(l.idContaDestino).nome : l.descricao

        })),
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
