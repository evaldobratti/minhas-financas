<template>
<div>
  <v-select
    ref="autoComplete"
    v-model="internal"
    :items="locais"
    item-text="nome"
    autocomplete
    @input="$emit('input', $event)"
    :label="label"
    :search-input.sync="maybeNewValue">
      <template slot="item" slot-scope="data">
        {{ data.item.nome }}
        <small v-if="data.item.id == null">&nbsp;nova</small>
      </template>
  </v-select>
</div>
</template>

<script>
export default {
  props: ['value', 'label'],
  data() {
    return {
      maybeNewValue: '',
      internal: null
    }
  },
  created() {
    this.internal = this.value;
  },
  watch: {
    value(val) {
      if (val != null) {
        this.maybeNewLocal(val.nome);
      }
      this.internal = val;
    },
    maybeNewValue(val) {
      this.maybeNewLocal(val);
    },
  },
  methods: {
    maybeNewLocal(val) {
      if (val == null)
        return;
      
      let possible = this.locais.find(c => c.nome.toLowerCase().startsWith(val.toLowerCase()));
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
    refresh() {
      if (this.value.id == null) {
        console.info('affe');
        this.$store.state.locais.list = this.$store.state.locais.list.filter(c => c.id != null)
        
        this.$store.state.locais.list = [...this.$store.state.locais.list, this.value ];
        
      }
      this.internal = this.value;
    },
    focus() {
      this.$refs.autoComplete.focus();  
      this.$refs.autoComplete.$refs.input.focus();
    },

  },
  computed: {
    locais() {
      return this.$store.state.locais.list;
    }
  }
}
</script>

<style>

</style>
