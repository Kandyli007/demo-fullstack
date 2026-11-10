<script setup lang="ts">
import TaskBoard from '@/components/TaskBoard.vue'
</script>

<template>
  <div class="page">
    <header class="hero">
      <div class="hero__glow" aria-hidden="true"></div>
      <div class="hero__content">
        <span class="hero__badge">Spring Boot 3 · Vue 3 · TypeScript</span>
        <h1>任务管理台</h1>
        <p>前后端分离示例：REST API 提供数据，Element Plus 负责交互。</p>
      </div>
    </header>

    <main class="content">
      <TaskBoard />
    </main>

    <footer class="footer">
      后端 <code>http://localhost:8080</code> · 前端 <code>http://localhost:5173</code>
    </footer>
  </div>
</template>

<style scoped>
.page {
  width: min(1180px, 100%);
  margin: 0 auto;
  padding: 40px 24px 32px;
}

.hero {
  position: relative;
  overflow: hidden;
  padding: 48px;
  border-radius: 28px;
  background: linear-gradient(135deg, #0f172a 0%, #172554 55%, #1d4ed8 130%);
  box-shadow: 0 24px 60px -24px rgba(15, 23, 42, 0.55);
}

.hero__glow {
  position: absolute;
  top: -120px;
  right: -60px;
  width: 340px;
  height: 340px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.55), transparent 68%);
  filter: blur(8px);
}

.hero__content {
  position: relative;
  z-index: 1;
}

.hero__badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border: 1px solid rgba(147, 197, 253, 0.35);
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.16);
  color: #bfdbfe;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero h1 {
  margin: 18px 0 10px;
  color: #f8fafc;
  font-size: clamp(32px, 5vw, 44px);
  font-weight: 700;
  letter-spacing: 0.02em;
}

.hero p {
  margin: 0;
  max-width: 520px;
  color: #c7d2fe;
  font-size: 15px;
  line-height: 1.7;
}

.content {
  position: relative;
  margin-top: -26px;
}

.footer {
  margin-top: 28px;
  color: #94a3b8;
  font-size: 12px;
  text-align: center;
}

.footer code {
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(148, 163, 184, 0.14);
  color: #64748b;
}

@media (max-width: 640px) {
  .page {
    padding: 20px 14px 24px;
  }

  .hero {
    padding: 32px 24px 44px;
    border-radius: 22px;
  }
}
</style>
