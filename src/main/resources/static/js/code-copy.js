(function () {
    function copyToClipboard(text) {
        if (navigator.clipboard && window.isSecureContext) {
            return navigator.clipboard.writeText(text);
        }

        return new Promise((resolve, reject) => {
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.readOnly = true;
            textarea.style.position = 'fixed';
            textarea.style.top = '-9999px';
            textarea.style.left = '-9999px';
            textarea.style.opacity = '0';
            document.body.appendChild(textarea);
            textarea.select();

            try {
                const success = document.execCommand('copy');
                document.body.removeChild(textarea);
                if (success) {
                    resolve();
                } else {
                    reject(new Error('copy failed'));
                }
            } catch (error) {
                document.body.removeChild(textarea);
                reject(error);
            }
        });
    }

    function initButton(button) {
        const copyText = (button.dataset.copyText || '').trim();
        if (!copyText) {
            return;
        }

        const defaultLabel = button.dataset.copyLabel || button.textContent.trim() || '复制';
        const copiedLabel = button.dataset.copiedLabel || '已复制';
        const errorLabel = button.dataset.copyErrorLabel || '复制失败';
        let resetTimer = null;

        button.addEventListener('click', async () => {
            if (button.dataset.copyState === 'busy') {
                return;
            }

            button.dataset.copyState = 'busy';
            button.disabled = true;
            if (resetTimer) {
                window.clearTimeout(resetTimer);
            }

            try {
                await copyToClipboard(copyText);
                button.textContent = copiedLabel;
                button.classList.add('is-copied');
                button.classList.remove('is-error');
            } catch (error) {
                button.textContent = errorLabel;
                button.classList.add('is-error');
                button.classList.remove('is-copied');
            } finally {
                resetTimer = window.setTimeout(() => {
                    button.textContent = defaultLabel;
                    button.classList.remove('is-copied', 'is-error');
                    button.disabled = false;
                    button.dataset.copyState = '';
                }, 1400);
            }
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('[data-copy-text]').forEach(initButton);
    });
})();
