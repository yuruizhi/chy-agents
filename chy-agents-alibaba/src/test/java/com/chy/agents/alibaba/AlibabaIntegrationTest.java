// 版本验证测试
@Test
void testVersionCompatibility() {
    assertThat(SpringVersion.getVersion()).startsWith("6.1.");
    assertThat(SpringAiVersion.getVersion()).startsWith("1.0");
} 