# SortBigText
for CUSTIS

Генератор создает временные файлы с учетом входных параметров (учитывает доступную ОЗУ)

Сортировщик:
- сортирует отдельные файлы
- отсортированные части сравниваются и объединяются в итоговый файл out.txt

Сделано 2 имплементации интерфейса Sorter
- SorterByPriorityQueue
- SorterByMap

Сложность O(kN) - k количество временных файлов

