#!/usr/bin/env bash
set -euo pipefail

BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}"
PARTITIONS="${KAFKA_DEFAULT_PARTITIONS:-3}"
REPLICATION_FACTOR="${KAFKA_REPLICATION_FACTOR:-1}"
TOPICS_FILE="${TOPICS_FILE:-/etc/kafka/topics.txt}"

echo "[topic-init] bootstrap=${BOOTSTRAP_SERVERS}, partitions=${PARTITIONS}, rf=${REPLICATION_FACTOR}"
echo "[topic-init] topics_file=${TOPICS_FILE}"

if [[ ! -f "${TOPICS_FILE}" ]]; then
  echo "[topic-init] ERROR: topics file not found: ${TOPICS_FILE}"
  exit 1
fi

create_topic() {
  local topic="$1"
  echo "[topic-init] ensure topic: ${topic}"
  /opt/kafka/bin/kafka-topics.sh --bootstrap-server "${BOOTSTRAP_SERVERS}" \
    --create --if-not-exists \
    --topic "${topic}" \
    --partitions "${PARTITIONS}" \
    --replication-factor "${REPLICATION_FACTOR}" >/dev/null
}

# Read topics.txt line by line
while IFS= read -r line || [[ -n "$line" ]]; do
  # trim
  topic="$(echo "$line" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
  # skip blanks and comments
  if [[ -z "${topic}" ]] || [[ "${topic}" == \#* ]]; then
    continue
  fi
  create_topic "${topic}"
done < "${TOPICS_FILE}"

echo "[topic-init] Done."
