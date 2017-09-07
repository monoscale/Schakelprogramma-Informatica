program="./87.sh";
declare -A weekdays;
for i in {0..6};
	do weekdays[$($program $i)]=$i;
done;
dag=$($program $(date +%w));
unset weekdays[$dag];
printf "De huidige dag is %s. De overige dagen in de week zijn:\n" ${dag,};
for d in ${!weekdays[@]};
	do echo $d;
done;
