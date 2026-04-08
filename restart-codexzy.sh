#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="${SCRIPT_DIR}/app.log"
PID_FILE="${SCRIPT_DIR}/app.pid"

find_jar() {
  local jar
  jar="$(find "${SCRIPT_DIR}" -maxdepth 1 -type f -name '*.jar' | head -n 1)"

  if [[ -z "${jar}" ]]; then
    echo "当前目录未找到 jar 文件：${SCRIPT_DIR}"
    exit 1
  fi

  echo "${jar}"
}

get_pid() {
  local pid jar_name

  if [[ -f "${PID_FILE}" ]]; then
    pid="$(cat "${PID_FILE}")"
    if [[ -n "${pid}" ]] && kill -0 "${pid}" 2>/dev/null; then
      echo "${pid}"
      return 0
    fi
  fi

  jar_name="$(basename "$(find_jar)")"
  ps -ef | grep '[j]ava' | grep -F -- "${jar_name}" | awk 'NR==1 {print $2}' || true
}

start() {
  local jar pid
  jar="$(find_jar)"
  pid="$(get_pid)"

  if [[ -n "${pid}" ]]; then
    echo "程序已在运行，PID=${pid}"
    return 0
  fi

  echo "启动 $(basename "${jar}") ..."
  nohup java -jar "${jar}" >> "${LOG_FILE}" 2>&1 &
  echo $! > "${PID_FILE}"
  echo "启动完成，PID=$!"
  echo "日志文件：${LOG_FILE}"
}

stop() {
  local pid
  pid="$(get_pid)"

  if [[ -z "${pid}" ]]; then
    echo "程序未运行"
    rm -f "${PID_FILE}"
    return 0
  fi

  echo "停止进程，PID=${pid} ..."
  kill "${pid}" 2>/dev/null || true
  rm -f "${PID_FILE}"
}

status() {
  local pid
  pid="$(get_pid)"

  if [[ -n "${pid}" ]]; then
    echo "程序运行中，PID=${pid}"
  else
    echo "程序未运行"
  fi
}

case "${1:-restart}" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  status)
    status
    ;;
  *)
    echo "用法: $0 {start|stop|restart|status}"
    echo "请把脚本和 jar 放在同一目录后再执行。"
    exit 1
    ;;
esac
