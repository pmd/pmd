function switchToRealPassword() {
    var real = $('realPass');
    var prompt = $('promptPass');
    real.style.display = 'inline';
    prompt.style.display = 'none';
    real.focus();
}
