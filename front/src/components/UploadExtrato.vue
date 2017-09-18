<template>
  <ProtectedRoute>
      <input type="file" @change="fileChanged" />
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from './ProtectedRoute'
import axios from 'axios';

export default {
  methods: {
    fileChanged(e) {
      const formData = new FormData();
      console.info(e.target.files[0].name);
      formData.append('extrato', e.target.files[0], e.target.files[0].name);
      axios.post('/api/1001/upload', formData).then(res => console.info(res)).then(err => console.info(err));
    }
  },
  components: {
    ProtectedRoute
  }
}
</script>

<style>

</style>
