(function () {
    function syncTargetCode(form) {
        const recordType = form.querySelector('select[name="recordType"]');
        const targetCode = form.querySelector('input[name="targetReportCode"]');
        if (!recordType || !targetCode) {
            return;
        }

        const ownCode = form.dataset.ownCode || '';
        const isEdit = form.dataset.editMode === 'true';
        if (isEdit) {
            targetCode.readOnly = true;
            return;
        }

        const updateState = () => {
            if (recordType.value === 'INBOUND') {
                targetCode.readOnly = true;
                targetCode.value = ownCode;
            } else {
                targetCode.readOnly = false;
                if (!targetCode.value || targetCode.value === ownCode) {
                    targetCode.value = '';
                }
            }
        };

        recordType.addEventListener('change', updateState);
        updateState();
    }

    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('form.business-form').forEach(syncTargetCode);
    });
})();
