(function () {
    function syncTargetCode(form) {
        const recordType = form.querySelector('select[name="recordType"]');
        const targetCode = form.querySelector('input[name="targetReportCode"]');
        const boundTargetSelect = form.querySelector('[data-bound-target-select]');
        const bindTargetCheckbox = form.querySelector('input[name="bindTarget"]');
        if (!recordType || !targetCode) {
            return;
        }

        const ownCode = form.dataset.ownCode || '';
        const isEdit = form.dataset.editMode === 'true';
        if (isEdit) {
            targetCode.readOnly = true;
            return;
        }

        const syncBoundTargetSelect = () => {
            if (!boundTargetSelect) {
                return;
            }
            const matchedOption = Array.from(boundTargetSelect.options).find((option) => option.value && option.value === targetCode.value);
            boundTargetSelect.value = matchedOption ? matchedOption.value : '';
        };

        const updateState = () => {
            if (recordType.value === 'INBOUND') {
                targetCode.readOnly = true;
                targetCode.value = ownCode;
                if (boundTargetSelect) {
                    boundTargetSelect.disabled = true;
                    boundTargetSelect.value = '';
                }
                if (bindTargetCheckbox) {
                    bindTargetCheckbox.checked = false;
                    bindTargetCheckbox.disabled = true;
                }
            } else {
                targetCode.readOnly = false;
                if (boundTargetSelect && boundTargetSelect.options.length > 1) {
                    boundTargetSelect.disabled = false;
                }
                if (bindTargetCheckbox) {
                    bindTargetCheckbox.disabled = false;
                }
                if (!targetCode.value || targetCode.value === ownCode) {
                    const selectedValue = boundTargetSelect && boundTargetSelect.value
                        ? boundTargetSelect.value
                        : (boundTargetSelect && boundTargetSelect.options.length > 1 ? boundTargetSelect.options[1].value : '');
                    targetCode.value = selectedValue;
                }
                syncBoundTargetSelect();
            }
        };

        if (boundTargetSelect) {
            boundTargetSelect.addEventListener('change', () => {
                if (boundTargetSelect.value) {
                    targetCode.value = boundTargetSelect.value;
                }
            });
        }
        targetCode.addEventListener('input', syncBoundTargetSelect);
        recordType.addEventListener('change', updateState);
        updateState();
    }

    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('form.business-form').forEach(syncTargetCode);
    });
})();
