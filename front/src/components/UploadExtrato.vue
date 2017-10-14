<template>
  <ProtectedRoute>
    <v-select
      label="Conta"
      v-bind:items="contas"
      v-model="conta"
      item-text="nome"
      max-height="auto">
    </v-select>
    <input type="file" @change="fileChanged" />
      <v-data-table
        :items="lancamentos"
        hide-actions
        class="elevation-5 lancamentos">
        <template slot="headers" slot-scope="props">
          <tr>
            <th style="width: 10px">
              Data
            </th>
            <th>
              Local
            </th>
            <th style="width: 130px">
              Categoria
            </th>
            <th style="text-align: right; width: 140px">
              Valor
            </th>
            <th style="text-align: right; width: 140px">
              Saldo
            </th>
            <th  style="width: 40px" ></th>
            <th  style="width: 40px" ></th>
          </tr>
        </template>
        <template slot="items" slot-scope="l">
          <LancamentoLinha :lancamento="l.item" />
        </template>
      </v-data-table>

  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from './ProtectedRoute'
import axios from 'axios';
import LancamentoLinha from './lancamento/LancamentoLinha'
import { normalizeLancamentos } from '../store/lancamento';

export default {
  data() {
    return {
      conta: null,
      lancamentos: []
    }
  },
  computed: {
    contas() {
      return this.$store.getters.getContas;
    }
  },
  methods: {
    fileChanged(e) {
      const formData = new FormData();
      console.info(e.target.files[0].name);
      formData.append('extrato', e.target.files[0], e.target.files[0].name);
      axios.post('/api/'+this.conta.id+'/upload', formData).then(res => {
        this.lancamentos = res.data;
        normalizeLancamentos(this.lancamentos, this.$store.getters);
      }).then(err => console.info(err));
    }
  },
  components: {
    ProtectedRoute,
    LancamentoLinha
  }
}
</script>

<style>

</style>
