package org.bratti.domain.enumeration;

import java.time.temporal.ChronoUnit;

/**
 * The TipoFrequencia enumeration.
 */
public enum TipoFrequencia {
    DIA {
		@Override
		public ChronoUnit unit() {
			return ChronoUnit.DAYS;
		}
	}, MES {
		@Override
		public ChronoUnit unit() {
			return ChronoUnit.MONTHS;
		}
	},  ANO {
		@Override
		public ChronoUnit unit() {
			return ChronoUnit.YEARS;
		}
	};
	
	public abstract ChronoUnit unit();
}
