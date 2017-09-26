<template>
<div>
  <v-select
    ref="autoComplete"
    :value="value"
    @input="$emit('input', $event)"
    :items="locais"
    item-text="nome"
    v-bind="$attrs"
    v-on="$listeners"
    autocomplete
    :search-input.sync="maybeNewValue">
      <template slot="item" scope="data">
        {{ data.item.nome }}
        <small v-if="data.item.id == null">&nbsp;nova</small>
      </template>
  </v-select>
</div>
</template>

<script>
export default {
  props: ['value'],
  data() {
    return {
      maybeNewValue: ''
    }
  },
  methods: {
    focus() {
      this.$refs.autoComplete.focus();  
      this.$refs.autoComplete.$refs.input.focus();
    }
  },
  watch: {
    maybeNewValue(val) {
      if (val == null)
        return;
      
      let possible = this.locais.find(c => c.nome.toLowerCase().indexOf(val.toLowerCase()) >= 0);
      if (possible == null) {
        const categoria = this.$store.state.locais.list.find(c => c.id == null);
        if (categoria) {
          categoria.nome = val;
        } else {
          this.$store.state.locais.list.push({
            nome: val
          })
        }
      }
    },
  },
  computed: {
    locais() {
      return this.$store.state.locais.list;
    },
  }
}
</script>

<style>

</style>
