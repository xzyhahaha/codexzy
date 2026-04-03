(function () {
    var dropzone = document.getElementById('memoDropzone');
    var input = document.getElementById('memoFileInput');
    var meta = document.getElementById('memoFileMeta');

    if (!dropzone || !input || !meta) {
        return;
    }

    function updateFileMeta(file) {
        if (!file) {
            meta.textContent = '当前还没有选择文件';
            dropzone.classList.remove('has-file');
            return;
        }

        var sizeMb = Math.max(file.size / 1024 / 1024, 0.01).toFixed(2);
        meta.textContent = '已选择：' + file.name + ' / ' + sizeMb + ' MB';
        dropzone.classList.add('has-file');
    }

    function assignFiles(fileList) {
        if (!fileList || !fileList.length) {
            return;
        }

        var transfer = new DataTransfer();
        transfer.items.add(fileList[0]);
        input.files = transfer.files;
        updateFileMeta(transfer.files[0]);
    }

    dropzone.addEventListener('click', function () {
        input.click();
    });

    dropzone.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            input.click();
        }
    });

    input.addEventListener('change', function () {
        updateFileMeta(input.files[0]);
    });

    ['dragenter', 'dragover'].forEach(function (type) {
        dropzone.addEventListener(type, function (event) {
            event.preventDefault();
            dropzone.classList.add('is-dragover');
        });
    });

    ['dragleave', 'dragend', 'drop'].forEach(function (type) {
        dropzone.addEventListener(type, function (event) {
            event.preventDefault();
            if (type !== 'drop') {
                dropzone.classList.remove('is-dragover');
            }
        });
    });

    dropzone.addEventListener('drop', function (event) {
        dropzone.classList.remove('is-dragover');
        assignFiles(event.dataTransfer.files);
    });

    document.addEventListener('paste', function (event) {
        if (!event.clipboardData || !event.clipboardData.items) {
            return;
        }

        for (var i = 0; i < event.clipboardData.items.length; i++) {
            var item = event.clipboardData.items[i];
            if (item.kind === 'file') {
                var file = item.getAsFile();
                if (file) {
                    assignFiles([file]);
                    dropzone.focus();
                    break;
                }
            }
        }
    });
})();